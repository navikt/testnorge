import React from 'react'
import _orderBy from 'lodash/orderBy'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { OrganisasjonItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import BestillingDetaljer from '~/components/bestilling/detaljer/Detaljer'
import { OrgStatus } from '~/components/fagsystem/organisasjoner/types'
import Spinner from '~/components/ui/loading/Spinner'
import Formatters from '~/utils/DataFormatter'

type OrganisasjonBestillingProps = {
	brukerId: string
	bestillinger: Array<OrgStatus>
}

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function OrganisasjonBestilling({
	brukerId,
	bestillinger,
}: OrganisasjonBestillingProps) {
	if (!bestillinger) {
		return null
	}

	const sortedOrgliste = _orderBy(bestillinger, ['id'], ['desc'])

	const columns = [
		{
			text: 'ID',
			width: '10',
			dataField: 'id',
			unique: true,
		},
		{
			text: 'Antall',
			width: '10',
			dataField: 'antallLevert',
		},
		{
			text: 'Sist oppdatert',
			width: '20',
			dataField: 'sistOppdatert',
			formatter(cell: string): string {
				return Formatters.formatDate(cell)
			},
		},
		{
			text: 'Miljø',
			width: '15',
			dataField: 'environments',
			formatter(cell: string) {
				return cell.toUpperCase().replaceAll(',', ', ')
			},
		},
		{
			text: 'Orgnr.',
			width: '15',
			dataField: 'organisasjonNummer',
			unique: true,
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'listedata[4]',
			formatter: (cell: string) => {
				return cell === 'Pågår' || cell === 'DEPLOYER' ? (
					<Spinner size={24} />
				) : (
					//@ts-ignore
					<Icon kind={ikonTypeMap[cell]} title={cell} />
				)
			},
		},
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
