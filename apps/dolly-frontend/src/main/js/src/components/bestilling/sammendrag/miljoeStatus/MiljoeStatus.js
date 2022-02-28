import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import FagsystemStatus from './fagsystemStatus/FagsystemStatus'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'

const mapStatusrapport = (bestillingstatus) => {
	const sortBestilling = (a, b) => {
		if (a.navn === b.navn) {
			return a.miljo > b.miljo ? 1 : -1
		}
	}

	const statusListe = []

	bestillingstatus.map((system) => {
		system.statuser.map((status) => {
			const systeminfo = {
				navn: system.navn,
				id: system.id,
				melding: status.melding !== 'OK' ? status.melding : null,
			}

			if (status.identer) {
				statusListe.push({
					...systeminfo,
					miljo: null,
					identer: status.identer,
				})
			}

			if (status.orgnummer) {
				statusListe.push({
					...systeminfo,
					miljo: null,
					orgnummer: status.orgnummer,
				})
			}

			if (status.detaljert) {
				status.detaljert.map((miljoInfo) => {
					const statusIndex = statusListe.findIndex((x) => x.id === system.id)
					if (
						statusIndex >= 0 &&
						(status.melding === statusListe[statusIndex].melding ||
							(status.melding === 'OK' && statusListe[statusIndex].melding === null))
					) {
						statusListe[statusIndex].miljo = statusListe[statusIndex].miljo.concat(
							', ',
							miljoInfo.miljo.toUpperCase()
						)
					} else {
						statusListe.push({
							...systeminfo,
							miljo: miljoInfo.miljo.toUpperCase(),
							identer: miljoInfo.identer,
						})
					}
				})
			}
		})
	})

	return statusListe.sort(sortBestilling)
}

export default function MiljoeStatus({ bestilling }) {
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')
	const statusrapport = mapStatusrapport(bestilling.status)
	const { tekst } = antallIdenterOpprettet(bestilling)

	return (
		<div>
			<SubOverskrift label="Bestillingsstatus" />
			{bestilling.systeminfo && (
				<div className="feilmelding_generell">
					{!erOrganisasjon && <p>{tekst}</p>}
					{statusrapport.length < 1 && (
						<ApiFeilmelding feilmelding={bestilling.systeminfo} container />
					)}
				</div>
			)}
			<FagsystemStatus statusrapportListe={statusrapport} />
		</div>
	)
}
