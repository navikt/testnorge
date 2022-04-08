import React, { Suspense, useEffect, useState } from 'react'
import { Route, Routes, useNavigate } from 'react-router-dom'
import Header from '~/components/layout/header/Header'
import Loading from '~/components/ui/loading/Loading'
import allRoutes from '~/Routes'
import { VarslingerModal } from '~/components/varslinger/VarslingerModal'
import './App.less'
import { Forbedring } from '~/components/feedback/Forbedring'
import Utlogging from '~/components/utlogging'
import { ProfilApi } from '~/service/Api'
import ToastConnector from '~/components/ui/toast/ToastConnector'
import { Breadcrumbs } from '~/components/layout/breadcrumb/Breadcrumb'
import { logoutBruker } from '~/components/utlogging/Utlogging'

type Props = {
	brukerData?: Object
	updateVarslingerBruker?: Function
	getEnvironments?: Function
	getCurrentBruker?: Function
}

export const App = ({
	brukerData,
	updateVarslingerBruker,
	getCurrentBruker,
	getEnvironments,
}: Props) => {
	const [criticalError, setCriticalError] = useState(null)
	const [brukerProfil, setBrukerProfil] = useState(undefined)
	const [brukerBilde, setBrukerBilde] = useState(undefined)
	const navigate = useNavigate()

	useEffect(() => {
		getCurrentBruker().catch((err: Object) => setCriticalError(err))
		getEnvironments().catch((err: Object) => setCriticalError(err))

		ProfilApi.getProfil()
			.then((response: { data: Object }) => setBrukerProfil(response.data))
			.catch(() => setBrukerProfil(null))
		ProfilApi.getProfilBilde()
			.then((response: { data: Response }) =>
				response.data.blob().then((blob) => setBrukerBilde(URL.createObjectURL(blob)))
			)
			.catch(() => setBrukerBilde(null))
	}, [])

	function extractFelmelding(stackTrace: string) {
		if (stackTrace?.includes('miljoer')) return 'miljoe_error'
		else if (stackTrace?.includes('current')) return 'azure_error'
		else return 'unknown_error'
	}

	const logout = (stackTrace: string) => {
		const feilmelding = extractFelmelding(stackTrace)
		logoutBruker(navigate, feilmelding)
	}

	if (criticalError) logout(criticalError.stack)

	if (!brukerData || brukerProfil === undefined || brukerBilde === undefined)
		return <Loading label="Laster Dolly applikasjon" fullpage />

	return (
		<React.Fragment>
			<Utlogging />
			<VarslingerModal updateVarslingerBruker={updateVarslingerBruker} />
			<Header brukerProfil={brukerProfil} brukerBilde={brukerBilde} />
			<Breadcrumbs />
			<main>
				<Suspense fallback={<Loading label="Laster inn" />}>
					<Routes>
						{allRoutes.map((route, idx) =>
							route.element ? (
								<Route
									key={idx}
									path={route.path}
									// @ts-ignore
									element={
										<route.element
											// @ts-ignore
											brukerBilde={brukerBilde}
											brukerProfil={brukerProfil}
										/>
									}
								/>
							) : (
								<React.Fragment />
							)
						)}
					</Routes>
				</Suspense>
			</main>
			<Forbedring brukerBilde={brukerBilde} />
			<ToastConnector />
		</React.Fragment>
	)
}
