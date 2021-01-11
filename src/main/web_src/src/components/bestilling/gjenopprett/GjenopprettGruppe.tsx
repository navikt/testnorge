import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import React from 'react'
import { GjenopprettModal } from '~/components/bestilling/gjenopprett/GjenopprettModal'
import { DollyApi } from '~/service/Api'
import bestilling from '~/ducks/bestilling'

type GjenopprettGruppe = {
	onCancel: any
	gruppe: Gruppe
	gruppeId: string
	gruppenavn: string
	bestillingStatuser: Record<string, BestillingStatus>
	getBestillinger: any
}

type Gruppe = {
	id: string
	navn: string
	antallIdenter: string
}

type BestillingStatus = {
	environments: Array<string>
}

export const GjenopprettGruppe = ({
	onCancel,
	gruppe,
	bestillingStatuser,
	getBestillinger
}: GjenopprettGruppe) => {
	const bestilteMiljoer = () => {
		const miljoer: Set<string> = new Set()
		Object.values(bestillingStatuser).forEach((bestillingstatus: BestillingStatus) =>
			bestillingstatus.environments.forEach((miljo: string) => miljoer.add(miljo))
		)
		return [...miljoer]
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>
				{gruppe.navn} - #{gruppe.id}
			</h1>
			<br />
			<div className="flexbox">
				<TitleValue title="Antall identer" value={gruppe.antallIdenter} />
				<TitleValue title="Bestilt miljø" value={Formatters.arrayToString(bestilteMiljoer())} />
			</div>
			<p>
				Alle personene i gruppen blir gjenopprettet til de valgte miljøene. Miljøene personene
				tidligere er bestilt til er allerede huket av. Dette kan du endre på, men husk at noen
				egenskaper er avhengig miljø.
			</p>
			<hr />
		</div>
	)

	const submitFormik = async (values: any) => {
		const envsQuery = Formatters.arrayToString(values.environments)
			.replace(/ /g, '')
			.toLowerCase()

		await DollyApi.gjenopprettGruppe(gruppe.id, envsQuery)
		await getBestillinger()
	}

	return (
		<GjenopprettModal
			gjenopprettHeader={gjenopprettHeader}
			submitFormik={submitFormik}
			closeModal={onCancel}
			environments={bestilteMiljoer()}
		/>
	)
}
