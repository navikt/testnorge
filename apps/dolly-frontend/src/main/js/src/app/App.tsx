import React, { Suspense, useEffect, useState } from 'react'
import { Route, Routes } from 'react-router-dom'
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
	const [brukerProfil, setBrukerProfil] = useState(null)
	const [brukerBilde, setBrukerBilde] = useState(undefined)

	useEffect(() => {
		getCurrentBruker().catch((err: Object) => setCriticalError(err))
		getEnvironments().catch((err: Object) => setCriticalError(err))

		ProfilApi.getProfil().then((response: { data: Object }) => setBrukerProfil(response.data))
		ProfilApi.getProfilBilde()
			.then((response: { data: Response }) =>
				response.data.blob().then((blob) => setBrukerBilde(URL.createObjectURL(blob)))
			)
			.catch(() => setBrukerBilde(null))
	}, [])

	const logout = (stackTrace: string) => {
		let feilmelding = 'unknown_error'
		if (stackTrace.includes('miljoer')) feilmelding = 'miljoe_error'
		else if (stackTrace.includes('current')) feilmelding = 'azure_error'

		window.location.href = '/logout?state=' + feilmelding
	}

	if (criticalError) logout(criticalError.stack)

	if (!brukerData || brukerBilde === undefined)
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
