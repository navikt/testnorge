import React from 'react'
import { useMount } from 'react-use'
import _last from 'lodash/last'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import PersonIBrukButtonConnector from '~/components/ui/button/PersonIBrukButton/PersonIBrukButtonConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'
import EtikettBase from 'nav-frontend-etiketter'

const etikettTypeMap = {
	Ferdig: 'suksess',
	Avvik: 'fokus'
}

export default function PersonListe({ isFetching, personListe, searchActive, fetchTpsfPersoner }) {
	useMount(fetchTpsfPersoner)

	if (isFetching) return <Loading label="laster personer" panel />

	if (!personListe || personListe.length === 0)
		return (
			<ContentContainer>
				Trykk på opprett personer-knappen for å starte en bestilling.
			</ContentContainer>
		)

	if (personListe.length <= 0 && searchActive) {
		return <ContentContainer>Søket gav ingen resultater.</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '15',
			dataField: 'ident.ident',
			unique: true
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'navn'
		},
		{
			text: 'Kjønn',
			width: '15',
			dataField: 'kjonn'
		},
		{
			text: 'Alder',
			width: '10',
			dataField: 'alder'
		},
		{
			text: 'Bestilling-ID',
			width: '20',
			dataField: 'ident.bestillingId',
			formatter: (cell, row) => {
				const arr = row.ident.bestillingId
				let str = arr[0]
				if (arr.length > 1) str = `${str} ...`
				return str
			}
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'status',
			formatter: (cell, row) => <EtikettBase type={etikettTypeMap[cell]}>{cell}</EtikettBase>
		},
		{
			text: 'Brukt',
			width: '10',
			dataField: 'ibruk',
			formatter: (cell, row) => <PersonIBrukButtonConnector ident={row.ident} />
		}
	]

	return (
		<DollyTable
			data={personListe}
			columns={columns}
			pagination
			rowIcon={bruker => (bruker.kjonn === 'MANN' ? 'man' : 'woman')}
			onExpand={bruker => (
				<PersonVisningConnector
					personId={bruker.ident.ident}
					bestillingId={_last(bruker.ident.bestillingId)}
					gruppeId={bruker.ident.gruppeId}
				/>
			)}
		/>
	)
}
