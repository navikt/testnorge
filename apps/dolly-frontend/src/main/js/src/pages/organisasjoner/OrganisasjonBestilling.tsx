import _ from 'lodash'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { BestillingIconItem } from '@/components/ui/icon/IconItem'
import Icon from '@/components/ui/icon/Icon'
import BestillingDetaljer from '@/components/bestilling/detaljer/BestillingDetaljer'
import { OrgStatus } from '@/components/fagsystem/organisasjoner/types'
import Spinner from '@/components/ui/loading/Spinner'
import { formatDateTime } from '@/utils/DataFormatter'
import bestillingStatusMapper from '@/ducks/bestillingStatus/bestillingStatusMapper'

type OrganisasjonBestillingProps = {
	brukerId: string
	brukertype: string
	bestillinger: Array<OrgStatus>
	sidetall: number
}

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function OrganisasjonBestilling({
	brukerId,
	brukertype,
	bestillinger,
}: OrganisasjonBestillingProps) {
	if (!bestillinger) {
		return null
	}

	const sortedOrgliste = _.orderBy(bestillinger, ['id'], ['desc'])
	// @ts-ignore
	const bestillingStatuser = bestillingStatusMapper(sortedOrgliste)

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
				return formatDateTime(cell)
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
				data={bestillingStatuser}
				columns={columns}
				pagination
				iconItem={<BestillingIconItem />}
				onExpand={(bestilling: OrgStatus) => {
					return (
						<BestillingDetaljer
							bestilling={bestilling}
							iLaastGruppe={null}
							brukerId={brukerId}
							brukertype={brukertype}
						/>
					)
				}}
			/>
		</ErrorBoundary>
	)
}
