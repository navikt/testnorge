import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import FagsystemStatus from './fagsystemStatus/FagsystemStatus'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '@/components/bestilling/utils/antallIdenterOpprettet'
import * as _ from 'lodash-es'
import React from 'react'

export type Miljostatus = {
	bestilling: {
		status: Bestillingsinformasjon[]
		systeminfo: string
		feil?: any
		antallIdenter?: number
		antallLevert?: number
		opprettetFraGruppeId?: number
	}
	erOrganisasjon?: boolean
}

export type Bestillingsinformasjon = {
	id: string
	navn: string
	statuser: Status[]
}

export type Detaljert = {
	miljo: string
	identer: string[]
	orgnummer: string
}

export type Status = {
	id: string
	navn: string
	melding: string
	miljo: string
	identer?: string[]
	orgnummer?: string
	detaljert?: Detaljert[]
}

const mapStatusrapport = (bestillingsinformasjonListe: Bestillingsinformasjon[]) => {
	const sortBestillingOnMiljoe = (first: Status, second: Status) => {
		if (first.navn === second.navn) {
			return first.miljo > second.miljo ? 1 : -1
		}
	}

	const statusListe: Status[] = []

	bestillingsinformasjonListe?.forEach((bestillingStatus: Bestillingsinformasjon) => {
		bestillingStatus.statuser?.forEach((status: Status) => {
			const systeminfo = {
				navn: bestillingStatus.navn,
				id: bestillingStatus.id,
				melding: status.melding,
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
				status.detaljert?.forEach((detaljertStatus: Detaljert) => {
					statusListe.push({
						...systeminfo,
						miljo: detaljertStatus.miljo?.toUpperCase(),
						identer: detaljertStatus.identer,
						orgnummer: detaljertStatus.orgnummer,
					})
				})
			}
		})
	})

	return mergeIdentiskeStatusmeldinger([...statusListe].sort(sortBestillingOnMiljoe))
}

const mergeIdentiskeStatusmeldinger = (statuser: Status[]) => {
	const unikeStatusMeldingerPerSystem = statuser
		?.map((status) => ({
			melding: status.melding,
			navn: status.navn,
			identer: status.identer,
		}))
		?.filter(
			(value, index, self) => index === self?.findIndex((status) => _.isEqual(status, value)),
		)

	return [...unikeStatusMeldingerPerSystem]?.map((statusMelding) =>
		statuser
			?.filter((status) => {
				const identerErIdentiske = _.isEqual(status.identer, statusMelding.identer)
				return (
					status.melding === statusMelding.melding &&
					status.navn === statusMelding.navn &&
					identerErIdentiske
				)
			})
			?.reduce((previousValue, currentValue) => ({
				...previousValue,
				miljo: `${previousValue.miljo}, ${currentValue.miljo}`,
			})),
	)
}

export default function MiljoeStatus({ bestilling }: Miljostatus) {
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
			<FagsystemStatus statusrapport={statusrapport} />
		</div>
	)
}
