import React from 'react'
import { useMount } from 'react-use'
import _last from 'lodash/last'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import PersonIBrukButtonConnector from '~/components/ui/button/PersonIBrukButton/PersonIBrukButtonConnector'
import PersonVisningConnector from '../PersonVisning/PersonVisningConnector'
import EtikettBase from 'nav-frontend-etiketter'
import Icon from '~/components/ui/icon/Icon'

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
			width: '20',
			dataField: 'identNr',
			unique: true
		},
		{
			text: 'Navn',
			width: '35',
			dataField: 'navn'
		},
		{
			text: 'Kjønn',
			width: '15',
			dataField: 'kjonn'
		},
		{
			text: 'Alder',
			width: '15',
			dataField: 'alder'
		},
		{
			text: 'Bestilling-ID',
			width: '20',
			dataField: 'bestillingId',
			formatter: (cell, row) => {
				const arr = row.bestillingId
				let str = arr[0]
				if (arr.length > 1) str = `${str} ...`
				return str
			}
		},
		{
			text: 'Status',
			width: '15',
			dataField: 'status',
			formatter: (cell, row) => <EtikettBase type={etikettTypeMap[cell]}>{cell}</EtikettBase>
		},
		{
			text: 'Kommentar',
			width: '20',
			dataField: 'harBeskrivelse',
			centerItem: true,
			formatter: (cell, row) => {
				if (row.ident.beskrivelse) {
					return <Icon kind="kommentar" size={20} />
				}
			}
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
					bestillingId={bruker.ident.bestillingId[0]}
					gruppeId={bruker.ident.gruppeId}
				/>
			)}
		/>
	)
}
