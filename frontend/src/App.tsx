import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import CompetitionSetupPage from './pages/CompetitionSetupPage';
import ScoringPage from './pages/ScoringPage';
import ReportPage from './pages/ReportPage';
import ParticipantWorkspacePage from './pages/ParticipantWorkspacePage';

function PrivateRoute({ children, role }: { children: React.ReactNode; role?: string }) {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('role');
  if (!token) return <Navigate to="/login" />;
  if (role && userRole !== role) return <Navigate to="/" />;
  return <>{children}</>;
}

export default function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="/competition/:id/setup" element={<PrivateRoute role="ADMIN"><CompetitionSetupPage /></PrivateRoute>} />
          <Route path="/competition/:id/score" element={<PrivateRoute role="JUDGE"><ScoringPage /></PrivateRoute>} />
          <Route path="/competition/:id/report" element={<PrivateRoute><ReportPage /></PrivateRoute>} />
          <Route path="/competition/:id/participant"
            element={<PrivateRoute role="PARTICIPANT"><ParticipantWorkspacePage /></PrivateRoute>} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}
