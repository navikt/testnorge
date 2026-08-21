import { BrowserRouter, Route, Routes } from 'react-router'
import StatusPage from '@/pages/StatusPage'

export default () => (
	<BrowserRouter>
		<Routes>
			<Route path="/*" element={<StatusPage />} />
		</Routes>
	</BrowserRouter>
)
