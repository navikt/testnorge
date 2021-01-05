import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { OrganisasjonItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import BestillingDetaljer from '~/components/bestilling/detaljer/Detaljer'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle'
}

export default function OrganisasjonBestilling({ orgListe }) {
	if (!orgListe) {
		return null
	}
	const columns = [
		{
			text: 'ID.',
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
			dataField: 'status[0].organisasjonsforvalterStatus',
			formatter: cell => {
				const status = cell.toUpperCase().includes('FEIL') ? 'Avvik' : 'Ferdig'
				return <Icon kind={ikonTypeMap[status]} title={status} />
			}
		}
	]

	return (
		<ErrorBoundary>
			<DollyTable
				data={orgListe}
				columns={columns}
				pagination
				iconItem={<OrganisasjonItem />}
				onExpand={bestilling => {
					return <BestillingDetaljer bestilling={bestilling} iLaastGruppe={null} />
				}}
			/>
		</ErrorBoundary>
	)
}
