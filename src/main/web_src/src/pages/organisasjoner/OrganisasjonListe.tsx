import React from 'react'
import _orderBy from 'lodash/orderBy'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap.css'
//@ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { OrganisasjonItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import { OrganisasjonVisning } from '~/components/fagsystem/organisasjoner/visning/Visning'
import { OrgStatus, EnhetData, EnhetBestilling } from '~/components/fagsystem/organisasjoner/types'

type OrganisasjonListe = {
	orgListe: OrgStatus
	bestillinger: Array<EnhetBestilling>
}

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle'
}

export default function OrganisasjonListe({ orgListe, bestillinger }: OrganisasjonListe) {
	if (!orgListe) {
		return null
	}

	const sortedOrgliste = _orderBy(orgListe, ['id'], ['desc'])

	const columns = [
		{
			text: 'Orgnr.',
			width: '20',
			dataField: 'organisasjonsnummer',
			unique: true,

			formatter: (cell: string, row: EnhetData) => (
				<div className="identnummer-cell">
					{row.organisasjonsnummer}
					<CopyToClipboard text={row.organisasjonsnummer}>
						<Tooltip
							overlay={'Kopier'}
							placement="top"
							destroyTooltipOnHide={true}
							mouseEnterDelay={0}
							mouseLeaveDelay={0.1}
							arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
							align={{
								offset: ['0', '-10']
							}}
						>
							<div
								className="icon"
								onClick={event => {
									event.stopPropagation()
								}}
							>
								<Icon kind="copy" size={15} />
							</div>
						</Tooltip>
					</CopyToClipboard>
				</div>
			)
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'organisasjonsnavn'
		},
		{
			text: 'Enhetstype',
			width: '15',
			dataField: 'enhetstype'
		},
		{
			text: 'Bestilling-ID',
			width: '20',
			dataField: 'bestillingId'
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'status',
			formatter(cell: string) {
				// @ts-ignore
				return <Icon kind={ikonTypeMap[cell]} title={cell} />
			}
		}
	]

	return (
		<ErrorBoundary>
			<DollyTable
				data={sortedOrgliste}
				columns={columns}
				pagination
				visSide={null}
				iconItem={<OrganisasjonItem />}
				onExpand={(organisasjon: EnhetData) => (
					<OrganisasjonVisning data={organisasjon} bestillinger={bestillinger} />
				)}
			/>
		</ErrorBoundary>
	)
}
