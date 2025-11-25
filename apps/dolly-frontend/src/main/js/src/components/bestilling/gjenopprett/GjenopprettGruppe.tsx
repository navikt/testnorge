import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString } from '@/utils/DataFormatter'

import { useDispatch } from 'react-redux'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'
import { DollyApi } from '@/service/Api'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useBestilteMiljoerForGruppe } from '@/utils/hooks/useBestilling'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { setUpdateNow } from '@/ducks/finnPerson'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

type GjenopprettGruppeProps = {
	onClose: any
	gruppeId: string
}

export const GjenopprettGruppe = ({ onClose, gruppeId }: GjenopprettGruppeProps) => {
	const dispatch = useDispatch()
	// const { currentBruker } = useCurrentBruker()
	const { gruppe } = useGruppeById(gruppeId)
	const { miljoer, loading } = useBestilteMiljoerForGruppe(gruppe.id)
	// const brukertype = currentBruker?.brukertype

	if (loading) {
		return <Loading label="Laster miljøer..." />
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h2>
				Gjenopprett gruppe: {gruppe.navn} (#{gruppe.id})
			</h2>
			<hr />
			<br />
			<div className="flexbox">
				<TitleValue title="Antall identer" value={gruppe.antallIdenter} />
				<TitleValue title="Bestilte miljø" value={arrayToString(miljoer)} size="medium" />
			</div>
			<p>
				Alle personene i gruppen blir gjenopprettet til de valgte miljøene. Miljøene personene
				tidligere er bestilt til er allerede huket av. Dette kan du endre på, men husk at noen
				egenskaper er avhengig av miljø.
			</p>
			<hr />
		</div>
	)

	const submitForm = async (values: any) => {
		const envsQuery = arrayToString(values.environments).replace(/ /g, '').toLowerCase()
		await DollyApi.gjenopprettGruppe(gruppe.id, envsQuery)
		dispatch(setUpdateNow())
		onClose()
	}

	return (
		<GjenopprettModal
			gjenopprettHeader={gjenopprettHeader}
			environments={miljoer}
			submitForm={submitForm}
			closeModal={onClose}
			// brukertype={brukertype}
		/>
	)
}
