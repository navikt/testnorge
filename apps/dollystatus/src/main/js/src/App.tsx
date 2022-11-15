import React from 'react'
// @ts-ignore
import ApplicationService from '@/services/ApplicationService'

import { BrowserRouter, Route, Routes } from 'react-router-dom'
import StatusPage from '@/pages/StatusPage'

export default () => (
	<BrowserRouter>
		<Routes>
			<Route path="/*" element={<StatusPage />} />
		</Routes>
	</BrowserRouter>
)
