import React from 'react'
import { useMount } from 'react-use'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
import PersonDetaljerConnector from '../PersonDetaljer/PersonDetaljerConnector'

export default function TestbrukerListe({
	isFetching,
	testidenter = [],
	testbrukerListe,
	editTestbruker,
	searchActive,
	username,
	getTPSFTestbrukere
}) {
	useMount(() => {
		if (testidenter.length) getTPSFTestbrukere()
	})

	if (isFetching) return <Loading label="laster testbrukere" panel />

	if (!testidenter)
		return (
			<ContentContainer>
				Trykk på opprett personer-knappen for å starte en bestilling.
			</ContentContainer>
		)

	if (!testbrukerListe) return null

	const testbrukereMedEnBestillingId = Formatters.flat2DArray(testbrukerListe, 5)
	const sortedTestbrukere = Formatters.sort2DArray(testbrukereMedEnBestillingId, 5)

	if (!sortedTestbrukere) return <Loading label="laster testbrukere" panel />

	if (sortedTestbrukere.length <= 0 && searchActive) {
		return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '15',
			dataField: '[0]'
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
				<PersonDetaljerConnector
					personId={bruker[0]}
					username={username}
					bestillingId={bruker[5]}
					editAction={() => editTestbruker(bruker[0])}
				/>
			)}
			pagination
		/>
	)
}
