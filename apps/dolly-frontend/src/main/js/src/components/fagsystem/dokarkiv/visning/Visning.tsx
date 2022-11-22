import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import DokarkivVisning from './DokarkivVisning'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '~/utils/hooks/useBestilling'
import Loading from '~/components/ui/loading/Loading'
import { Journalpost } from '~/service/services/JoarkDokumentService'

interface Form {
	data?: Array<MiljoDataListe>
	bestillingIdListe: Array<string>
	loading: boolean
}

type MiljoDataListe = {
	miljo: string
	data: Array<Journalpost>
}

type DokarkivTypes = {
	data?: Journalpost
	miljo?: string
}

const Dokarkiv = ({ data, miljo }: DokarkivTypes) => {
	if (!data) return null

	return <DokarkivVisning journalpost={data} miljoe={miljo} />
}

export default ({ data, bestillingIdListe, loading }: Form) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'dokarkiv')

	if (loading) {
		return <Loading label="Laster dokument-data" />
	}

	if (!data) {
		return null
	}

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	return (
		<>
			<SubOverskrift label="Dokumenter" iconKind="dokarkiv" />
			<MiljoTabs bestilteMiljoer={bestilteMiljoer} forsteMiljo={forsteMiljo} data={data}>
				<Dokarkiv />
			</MiljoTabs>
		</>
	)
}
