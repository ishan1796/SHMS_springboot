import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { api } from "../services/api";
import { Card } from "../components/common/Card";
import { Badge } from "../components/common/Badge";
import { Button } from "../components/common/Button";
import { Modal } from "../components/common/Modal";
import { Input } from "../components/common/Input";
import { Table } from "../components/common/Table";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { EmptyState } from "../components/common/EmptyState";
import {
  Calendar,
  Pill,
  FileText,
  CreditCard,
  Plus,
  AlertCircle,
  HeartPulse,
  Bot
} from "lucide-react";

export const PatientPortalPage: React.FC = () => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const [patientData, setPatientData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [bookModalOpen, setBookModalOpen] = useState(false);

  const [doctors, setDoctors] = useState<any[]>([]);
  const [slots, setSlots] = useState<any[]>([]);
  const [slotsLoading, setSlotsLoading] = useState(false);
  const [bookForm, setBookForm] = useState({
    doctorId: "",
    departmentId: "",
    appointmentDate: new Date().toISOString().split("T")[0],
    timeSlot: "",
    reason: "",
  });
  const [bookLoading, setBookLoading] = useState(false);

  // Sub-route detector
  const getActiveView = () => {
    const path = location.pathname;
    if (path.includes("/patient/appointments")) return "appointments";
    if (path.includes("/patient/prescriptions")) return "prescriptions";
    if (path.includes("/patient/reports")) return "reports";
    if (path.includes("/patient/bills")) return "bills";
    return "overview";
  };

  const activeView = getActiveView();

  const fetchDoctors = async () => {
    try {
      const res = await api.get("/appointments/doctors");
      if (res.data.success && res.data.doctors.length > 0) {
        setDoctors(res.data.doctors);
        if (!bookForm.doctorId) {
          setBookForm((prev) => ({
            ...prev,
            doctorId: res.data.doctors[0].id,
            departmentId: res.data.doctors[0].departmentId,
          }));
        }
      } else {
        // Fallback to appointments
        const apptRes = await api.get("/appointments");
        if (apptRes.data.success && apptRes.data.appointments.length > 0) {
          const uniqueDocs = Array.from(
            new Map(apptRes.data.appointments.map((a: any) => [a.doctorId, a.doctor])).values()
          ).filter(Boolean);
          setDoctors(uniqueDocs);
          if (uniqueDocs.length > 0 && !bookForm.doctorId) {
            setBookForm((prev) => ({
              ...prev,
              doctorId: (uniqueDocs[0] as any).id,
              departmentId: (uniqueDocs[0] as any).departmentId,
            }));
          }
        }
      }
    } catch (err) {
      console.error("Error fetching doctors:", err);
    }
  };

  const fetchSlots = async (doctorId: string, date: string) => {
    if (!doctorId || !date) return;
    try {
      setSlotsLoading(true);
      const res = await api.get("/appointments/slots", {
        params: { doctorId, date },
      });
      if (res.data.success) {
        setSlots(res.data.slots);
        // Automatically select the first available slot if current slot is empty or invalid
        const firstAvail = res.data.slots.find((s: any) => s.isAvailable);
        if (firstAvail) {
          setBookForm((prev) => ({ ...prev, timeSlot: firstAvail.timeSlot }));
        } else {
          setBookForm((prev) => ({ ...prev, timeSlot: "" }));
        }
      }
    } catch (err) {
      console.error("Error fetching slots:", err);
    } finally {
      setSlotsLoading(false);
    }
  };

  const fetchPatientProfile = async () => {
    try {
      setLoading(true);
      if (user?.patientId) {
        const res = await api.get(`/patients/${user.patientId}`);
        if (res.data.success) {
          setPatientData(res.data.patient);
        }
      }
      await fetchDoctors();
    } catch (err) {
      console.error("Error fetching patient data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatientProfile();
  }, [user?.patientId]);

  useEffect(() => {
    if (bookModalOpen && bookForm.doctorId && bookForm.appointmentDate) {
      fetchSlots(bookForm.doctorId, bookForm.appointmentDate);
    }
  }, [bookModalOpen, bookForm.doctorId, bookForm.appointmentDate]);

  const handleBookAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    setBookLoading(true);
    try {
      const res = await api.post("/appointments", {
        doctorId: bookForm.doctorId,
        departmentId: bookForm.departmentId,
        appointmentDate: bookForm.appointmentDate,
        timeSlot: bookForm.timeSlot,
        reason: bookForm.reason,
      });
      if (res.data.success) {
        setBookModalOpen(false);
        alert("Appointment scheduled successfully!");
        fetchPatientProfile();
      }
    } catch (err: any) {
      alert(err.response?.data?.message || "Failed to book appointment");
    } finally {
      setBookLoading(false);
    }
  };

  if (loading) return <LoadingSpinner label="Loading your patient health record..." />;

  const p = patientData || {};

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Patient Header Banner */}
      <div className="bg-gradient-to-r from-teal-900 via-slate-900 to-emerald-950 rounded-2xl p-6 text-white shadow-md border border-teal-700/40 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-teal-500/20 border border-teal-400/40 flex items-center justify-center text-2xl font-bold text-teal-300">
            {p.firstName?.[0]}{p.lastName?.[0]}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-xl font-bold">{p.firstName} {p.lastName}</h2>
              <span className="px-2 py-0.5 rounded-full text-xs font-mono font-bold bg-teal-500/20 text-teal-300 border border-teal-500/30">
                {p.uhid || "UHID-00001"}
              </span>
            </div>
            <p className="text-xs text-slate-300 mt-1">
              Gender: <strong className="text-white">{p.gender}</strong> • Blood Group: <strong className="text-white">{p.bloodGroup || "O+"}</strong> • Phone: <strong className="text-white">{p.phone}</strong>
            </p>
            {p.allergies && (
              <p className="text-xs text-amber-300 mt-1 flex items-center gap-1 font-medium">
                <AlertCircle className="w-3.5 h-3.5" /> Allergies: {p.allergies}
              </p>
            )}
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          {/* Sub-route Navigation Pills */}
          <div className="flex flex-wrap gap-1 bg-slate-800/80 p-1.5 rounded-xl border border-slate-700">
            <button
              onClick={() => navigate("/patient")}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeView === "overview" ? "bg-teal-600 text-white shadow" : "text-slate-300 hover:text-white"
              }`}
            >
              <HeartPulse className="w-3.5 h-3.5" />
              <span>Overview</span>
            </button>
            <button
              onClick={() => navigate("/patient/appointments")}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeView === "appointments" ? "bg-teal-600 text-white shadow" : "text-slate-300 hover:text-white"
              }`}
            >
              <Calendar className="w-3.5 h-3.5" />
              <span>Appointments ({(p.appointments || []).length})</span>
            </button>
            <button
              onClick={() => navigate("/patient/prescriptions")}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeView === "prescriptions" ? "bg-teal-600 text-white shadow" : "text-slate-300 hover:text-white"
              }`}
            >
              <Pill className="w-3.5 h-3.5" />
              <span>Prescriptions ({(p.prescriptions || []).length})</span>
            </button>
            <button
              onClick={() => navigate("/patient/reports")}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeView === "reports" ? "bg-teal-600 text-white shadow" : "text-slate-300 hover:text-white"
              }`}
            >
              <FileText className="w-3.5 h-3.5" />
              <span>Reports ({(p.medicalReports || []).length})</span>
            </button>
            <button
              onClick={() => navigate("/patient/bills")}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeView === "bills" ? "bg-teal-600 text-white shadow" : "text-slate-300 hover:text-white"
              }`}
            >
              <CreditCard className="w-3.5 h-3.5" />
              <span>Bills</span>
            </button>
          </div>

          <Button
            onClick={() => setBookModalOpen(true)}
            icon={<Plus className="w-4 h-4" />}
            className="bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold shadow-md"
          >
            Book Consult
          </Button>
        </div>
      </div>

      {/* Overview Grid */}
      {(activeView === "overview" || activeView === "appointments") && (
        <Card
          title={
            <div className="flex items-center justify-between w-full">
              <div className="flex items-center gap-2">
                <Calendar className="w-5 h-5 text-teal-600" />
                <span>My Appointments</span>
              </div>
              <Button size="sm" variant="outline" onClick={() => setBookModalOpen(true)}>
                + Schedule Appointment
              </Button>
            </div>
          }
          subtitle="Scheduled doctor consultations & OPD visits"
        >
          <Table
            columns={[
              {
                header: "Date & Time",
                accessor: (r: any) => (
                  <div>
                    <div className="font-semibold text-slate-800">{new Date(r.appointmentDate).toLocaleDateString()}</div>
                    <div className="text-xs text-slate-500">{r.timeSlot}</div>
                  </div>
                ),
              },
              {
                header: "Doctor / Specialty",
                accessor: (r: any) => (
                  <div>
                    <div className="font-medium text-slate-800">
                      Dr. {r.doctor?.employee?.user?.firstName} {r.doctor?.employee?.user?.lastName}
                    </div>
                    <div className="text-xs text-slate-500">{r.department?.name}</div>
                  </div>
                ),
              },
              {
                header: "Status",
                accessor: (r: any) => {
                  const variants: any = {
                    COMPLETED: "success",
                    SCHEDULED: "primary",
                    IN_CONSULTATION: "warning",
                    CANCELLED: "danger",
                  };
                  return <Badge variant={variants[r.status] || "neutral"}>{r.status}</Badge>;
                },
              },
              {
                header: "Token #",
                accessor: (r: any) => <span className="font-mono font-bold text-teal-700">#{r.tokenNumber}</span>,
              },
            ]}
            data={p.appointments || []}
            keyExtractor={(r: any) => r.id}
            emptyMessage="No appointment bookings found."
          />
        </Card>
      )}

      {(activeView === "overview" || activeView === "prescriptions") && (
        <Card
          title={
            <div className="flex items-center gap-2">
              <Pill className="w-5 h-5 text-amber-600" />
              <span>Active Prescriptions & Regimen</span>
            </div>
          }
          subtitle="Medications prescribed by your attending physicians"
        >
          {(!p.prescriptions || p.prescriptions.length === 0) ? (
            <EmptyState title="No Prescriptions" description="You have no recorded prescriptions on file." />
          ) : (
            <div className="space-y-4">
              {p.prescriptions.map((rx: any) => (
                <div key={rx.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200/80 space-y-2">
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-xs font-semibold text-slate-500">Date: {new Date(rx.date).toLocaleDateString()}</span>
                      <p className="text-xs font-bold text-slate-800">
                        Dr. {rx.doctor?.employee?.user?.firstName} {rx.doctor?.employee?.user?.lastName}
                      </p>
                    </div>
                    <Badge variant={rx.status === "DISPENSED" ? "success" : "warning"}>{rx.status}</Badge>
                  </div>
                  <div className="space-y-1.5 pt-2 border-t border-slate-200">
                    {(rx.items || []).map((item: any) => (
                      <div key={item.id} className="flex items-center justify-between text-xs bg-white p-2 rounded-lg border border-slate-100">
                        <div>
                          <strong className="text-slate-800">{item.medicineName}</strong>
                          <span className="text-slate-500 ml-2">({item.dosage}, {item.frequency})</span>
                        </div>
                        <span className="text-slate-600 font-mono font-medium">{item.durationDays} days ({item.quantity} units)</span>
                      </div>
                    ))}
                  </div>
                  {rx.instructions && (
                    <p className="text-xs text-slate-600 italic bg-amber-50/60 p-2 rounded border border-amber-100">
                      Doctor's Note: {rx.instructions}
                    </p>
                  )}
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {(activeView === "overview" || activeView === "reports") && (
        <Card
          title={
            <div className="flex items-center gap-2">
              <FileText className="w-5 h-5 text-cyan-600" />
              <span>Diagnostic & Lab Reports</span>
            </div>
          }
          subtitle="Pathology, hematology, and radiology results"
        >
          <Table
            columns={[
              { header: "Report Title", accessor: "title", className: "font-semibold text-slate-800" },
              { header: "Type", accessor: (r: any) => <Badge variant="info">{r.type}</Badge> },
              { header: "Date", accessor: (r: any) => new Date(r.reportDate).toLocaleDateString() },
            ]}
            data={p.medicalReports || []}
            keyExtractor={(r: any) => r.id}
            emptyMessage="No diagnostic reports available yet."
          />
        </Card>
      )}

      {(activeView === "overview" || activeView === "bills") && (
        <Card
          title={
            <div className="flex items-center gap-2">
              <CreditCard className="w-5 h-5 text-indigo-600" />
              <span>Hospital Invoices & Settlement Records</span>
            </div>
          }
          subtitle="Itemized hospital billing, payments, and outstanding balances"
        >
          <Table
            columns={[
              { header: "Invoice #", accessor: (r: any) => <span className="font-mono font-bold text-slate-800">{r.invoiceNumber}</span> },
              { header: "Total", accessor: (r: any) => `₹${r.finalAmount.toLocaleString()}` },
              { header: "Paid", accessor: (r: any) => `₹${r.paidAmount.toLocaleString()}` },
              {
                header: "Balance",
                accessor: (r: any) => (
                  <span className={r.balanceAmount > 0 ? "font-bold text-rose-600" : "text-emerald-600 font-semibold"}>
                    ₹{r.balanceAmount.toLocaleString()}
                  </span>
                ),
              },
              {
                header: "Status",
                accessor: (r: any) => (
                  <Badge variant={r.status === "PAID" ? "success" : r.status === "PARTIALLY_PAID" ? "warning" : "neutral"}>
                    {r.status}
                  </Badge>
                ),
              },
            ]}
            data={p.invoices || []}
            keyExtractor={(r: any) => r.id}
            emptyMessage="No billing statements generated."
          />
        </Card>
      )}

      {/* Book Appointment Modal */}
      <Modal
        isOpen={bookModalOpen}
        onClose={() => setBookModalOpen(false)}
        title="Schedule OPD Consultation (10-Minute Slots)"
        maxWidth="lg"
      >
        <form onSubmit={handleBookAppointment} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">Select Doctor / Specialty</label>
              <select
                value={bookForm.doctorId}
                onChange={(e) => {
                  const doc = doctors.find((d) => d.id === e.target.value);
                  setBookForm({
                    ...bookForm,
                    doctorId: e.target.value,
                    departmentId: doc?.departmentId || "",
                  });
                }}
                className="w-full rounded-lg border border-slate-300 text-sm px-3.5 py-2 text-slate-800"
              >
                {doctors.map((d) => (
                  <option key={d.id} value={d.id}>
                    Dr. {d.employee?.user?.firstName || d.firstName} {d.employee?.user?.lastName || d.lastName} ({d.specialization || "General"} - {d.department?.name || "OPD"})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">Appointment Date</label>
              <input
                type="date"
                required
                value={bookForm.appointmentDate}
                min={new Date().toISOString().split("T")[0]}
                onChange={(e) => setBookForm({ ...bookForm, appointmentDate: e.target.value })}
                className="w-full rounded-lg border border-slate-300 text-sm px-3.5 py-2 text-slate-800"
              />
            </div>
          </div>

          {/* 10-Minute Slot Matrix */}
          <div className="p-3 bg-slate-50 rounded-xl border border-slate-200/80 space-y-2">
            <div className="flex items-center justify-between">
              <div>
                <label className="block text-xs font-bold text-slate-800">
                  Select 10-Minute Time Slot ({bookForm.timeSlot ? `Selected: ${bookForm.timeSlot}` : "Please pick a slot"})
                </label>
                <p className="text-[11px] text-slate-500">
                  Each OPD consultation is strictly booked in 10-minute intervals to prevent queue congestion.
                </p>
              </div>
              {slotsLoading && <span className="text-xs text-teal-600 font-semibold animate-pulse">Checking availability...</span>}
            </div>

            {slots.length === 0 && !slotsLoading ? (
              <div className="text-xs text-slate-500 py-3 text-center">No slots available for this doctor on selected date.</div>
            ) : (
              <div className="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 gap-1.5 max-h-48 overflow-y-auto p-1 bg-white rounded-lg border border-slate-200">
                {slots.map((s: any) => {
                  const isSelected = bookForm.timeSlot === s.timeSlot;
                  const isAvail = s.isAvailable;
                  return (
                    <button
                      key={s.timeSlot}
                      type="button"
                      disabled={!isAvail}
                      onClick={() => setBookForm((prev) => ({ ...prev, timeSlot: s.timeSlot }))}
                      className={`px-2 py-1.5 rounded text-xs font-mono font-medium transition-all text-center flex flex-col items-center justify-center ${
                        isSelected
                          ? "bg-teal-600 text-white font-bold shadow ring-2 ring-teal-400"
                          : isAvail
                          ? "bg-emerald-50 text-emerald-800 border border-emerald-200 hover:bg-emerald-100 hover:border-emerald-400"
                          : "bg-slate-100 text-slate-400 border border-slate-200 cursor-not-allowed line-through"
                      }`}
                      title={isAvail ? "Available (Click to book)" : "Already Booked"}
                    >
                      <span>{s.timeSlot}</span>
                      <span className="text-[9px] font-sans font-semibold scale-90">
                        {isSelected ? "SELECTED" : isAvail ? "AVAILABLE" : "BOOKED"}
                      </span>
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          <Input
            label="Reason for Visit / Symptoms"
            required
            placeholder="e.g. Chest tightness, headache, routine checkup..."
            value={bookForm.reason}
            onChange={(e) => setBookForm({ ...bookForm, reason: e.target.value })}
          />

          <div className="pt-3 border-t flex justify-end gap-2">
            <Button variant="secondary" type="button" onClick={() => setBookModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" loading={bookLoading} disabled={!bookForm.timeSlot}>
              Confirm Appointment
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
