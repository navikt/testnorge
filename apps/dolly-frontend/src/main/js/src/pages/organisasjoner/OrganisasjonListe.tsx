import React from 'react'
import _orderBy from 'lodash/orderBy'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import 'rc-tooltip/assets/bootstrap.css'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { OrganisasjonItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import { OrganisasjonVisning } from '~/components/fagsystem/organisasjoner/visning/Visning'
import { EnhetBestilling, EnhetData, OrgStatus } from '~/components/fagsystem/organisasjoner/types'
import { CopyButton } from '~/components/ui/button/CopyButton/CopyButton'

type OrganisasjonListeProps = {
	bestillinger: Array<EnhetBestilling>
	organisasjoner: OrgStatus
}

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function OrganisasjonListe({
	bestillinger,
	organisasjoner,
}: OrganisasjonListeProps) {
	if (!organisasjoner) {
		return null
	}

	const sortedOrgliste = _orderBy(organisasjoner, ['id'], ['desc'])

	const columns = [
		{
			text: 'Orgnr.',
			width: '20',
			dataField: 'organisasjonsnummer',
			unique: true,

			formatter: (_cell: string, row: EnhetData) => <CopyButton value={row.organisasjonsnummer} />,
		},
		{
			text: 'Navn',
			width: '30',
			dataField: 'organisasjonsnavn',
		},
		{
			text: 'Enhetstype',
			width: '15',
			dataField: 'enhetstype',
		},
		{
			text: 'Bestilling-ID',
			width: '20',
			dataField: 'bestillingId',
			formatter: (_cell: number, row: EnhetData) => {
				const str = row.bestillingId
				if (str.length > 1) return `${str[0]} ...`
				return str[0]
			},
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'status',
			formatter(cell: string) {
				// @ts-ignore
				return <Icon kind={ikonTypeMap[cell]} title={cell} />
			},
		},
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
