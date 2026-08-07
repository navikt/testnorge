import { arrayToString } from '@/utils/DataFormatter'
import { useDispatch } from 'react-redux'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'
import { DollyApi } from '@/service/Api'
import { useBestilteMiljoerForGruppe } from '@/utils/hooks/useBestilling'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { setUpdateNow } from '@/ducks/finnPerson'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

type GjenopprettGruppeProps = {
	gruppeId: string
}

export const GjenopprettGruppe = ({ gruppeId }: GjenopprettGruppeProps) => {
	const dispatch = useDispatch()

	const { gruppe } = useGruppeById(gruppeId)
	const { miljoer, loading } = useBestilteMiljoerForGruppe(gruppe.id)
	const antallPersoner = gruppe.antallIdenter

	if (loading) {
		return <Loading label="Laster miljøer..." />
	}

	const submitForm = async (values: any) => {
		const envsQuery = arrayToString(values.environments).replace(/ /g, '').toLowerCase()
		await DollyApi.gjenopprettGruppe(gruppe?.id, envsQuery)
		dispatch(setUpdateNow())
	}

	return (
		<GjenopprettModal
			environments={miljoer}
			submitForm={submitForm}
			title={`Gjenopprett gruppe: ${gruppe?.navn} (#${gruppe?.id})`}
			beskrivelse="Alle personene i gruppen blir gjenopprettet til de valgte miljøene. Miljøene personene
				tidligere er bestilt til er allerede huket av. Dette kan du endre på, men husk at noen
				egenskaper er avhengig av miljø."
			antallIdenter={antallPersoner}
			bestilteMiljoer={miljoer}
			disabled={antallPersoner < 1}
			disabledTitle={antallPersoner < 1 ? 'Kan ikke gjenopprette en tom gruppe' : undefined}
		/>
	)
}
