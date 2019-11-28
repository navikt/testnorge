import React from 'react'
import { useMount } from 'react-use'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
// import PersonDetaljerConnector from '../PersonDetaljer/PersonDetaljerConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'

export default function TestbrukerListe({
	isFetching,
	testbrukerListe,
	searchActive,
	getTPSFTestbrukere,
	gruppeId
}) {
	useMount(getTPSFTestbrukere)

	if (isFetching) return <Loading label="laster testbrukere" panel />

	if (!testbrukerListe || testbrukerListe.length === 0)
		return (
			<ContentContainer>
				Trykk på opprett personer-knappen for å starte en bestilling.
			</ContentContainer>
		)

	const testbrukereMedEnBestillingId = Formatters.flat2DArray(testbrukerListe, 5)
	const sortedTestbrukere = Formatters.sort2DArray(testbrukereMedEnBestillingId, 5)

	if (sortedTestbrukere.length <= 0 && searchActive) {
		return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '15',
			dataField: '[0]',
			unique: true
		},
		{
			text: 'Type',
			width: '15',
			dataField: '[1]'
		},
		{
			text: 'Navn',
			width: '30',
			dataField: '[2]'
		},
		{
			text: 'Kjønn',
			width: '20',
			dataField: '[3]'
		},
		{
			text: 'Alder',
			width: '10',
			dataField: '[4]'
		},
		{
			text: 'Bestilling-ID',
			width: '10',
			dataField: '[5]'
		}
	]

	return (
		<DollyTable
			data={sortedTestbrukere}
			columns={columns}
			onExpand={bruker => (
				<PersonVisningConnector personId={bruker[0]} bestillingId={bruker[5]} gruppeId={gruppeId} />
			)}
			pagination
		/>
	)
}
