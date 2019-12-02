import React from 'react'
import { useMount } from 'react-use'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
// import PersonDetaljerConnector from '../PersonDetaljer/PersonDetaljerConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'

export default function TestbrukerListe({
	isFetching,
	testbrukerListe,
	searchActive,
	fetchTpsfTestbrukere,
	gruppeId
}) {
	useMount(fetchTpsfTestbrukere)

	if (isFetching) return <Loading label="laster testbrukere" panel />

	if (!testbrukerListe || testbrukerListe.length === 0)
		return (
			<ContentContainer>
				Trykk på opprett personer-knappen for å starte en bestilling.
			</ContentContainer>
		)

	if (testbrukerListe.length <= 0 && searchActive) {
		return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '15',
			dataField: 'ident',
			unique: true
		},
		{
			text: 'Type',
			width: '15',
			dataField: 'identtype'
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'navn'
		},
		{
			text: 'Kjønn',
			width: '20',
			dataField: 'kjonn'
		},
		{
			text: 'Alder',
			width: '10',
			dataField: 'alder'
		},
		{
			text: 'Bestilling-ID',
			width: '10',
			dataField: 'bestillingId'
		}
	]

	return (
		<DollyTable
			data={testbrukerListe}
			columns={columns}
			onExpand={bruker => (
				<PersonVisningConnector
					personId={bruker.ident}
					bestillingId={bruker.bestillingId}
					gruppeId={bruker.gruppeId}
				/>
			)}
			pagination
		/>
	)
}
