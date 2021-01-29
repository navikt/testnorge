import React from 'react'
import _orderBy from 'lodash/orderBy'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { OrganisasjonItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import BestillingDetaljer from '~/components/bestilling/detaljer/Detaljer'
import { OrgStatus } from '~/components/fagsystem/organisasjoner/types'
import Spinner from '~/components/ui/loading/Spinner'

type OrganisasjonBestilling = {
	orgListe: OrgStatus
	brukerId: string
}

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle'
}

export default function OrganisasjonBestilling({ orgListe, brukerId }: OrganisasjonBestilling) {
	if (!orgListe) {
		return null
	}

	const sortedOrgliste = _orderBy(orgListe, ['id'], ['desc'])

	const columns = [
		{
			text: 'ID',
			width: '10',
			dataField: 'id',
			unique: true
		},
		{
			text: 'Antall',
			width: '10',
			dataField: 'antallLevert'
		},
		{
			text: 'Sist oppdatert',
			width: '20',
			dataField: 'sistOppdatert',
			formatter(cell: string): string {
				return new Date(cell).toLocaleDateString()
			}
		},
		{
			text: 'Miljø',
			width: '15',
			dataField: 'environments',
			formatter(cell: string) {
				return cell.toUpperCase().replaceAll(',', ', ')
			}
		},
		{
			text: 'Orgnr.',
			width: '15',
			dataField: 'status[0].organisasjonsnummer',
			unique: true
		},
		{
			text: 'Status',
			width: '10',
			// TODO: Kan være flere statuser - fiks dette
			dataField: 'status[0].statuser[0].melding',
			formatter: (cell: string) => {
				const status =
					cell.toUpperCase().includes('FEIL') || cell.toUpperCase().includes('ERROR')
						? 'Avvik'
						: 'Ferdig'
				return cell === 'Pågående' || cell === 'DEPLOYER' ? (
					<Spinner size={24} />
				) : (
					<Icon kind={ikonTypeMap[status]} title={status} />
				)
			}
		}
	]

	return (
		<ErrorBoundary>
			{/* @ts-ignore */}
			<DollyTable
				data={sortedOrgliste}
				columns={columns}
				pagination
				iconItem={<OrganisasjonItem />}
				onExpand={(bestilling: OrgStatus) => {
					return (
						<BestillingDetaljer bestilling={bestilling} iLaastGruppe={null} brukerId={brukerId} />
					)
				}}
			/>
		</ErrorBoundary>
	)
}
