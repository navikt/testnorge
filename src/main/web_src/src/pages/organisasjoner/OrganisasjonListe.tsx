import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { OrganisasjonItem } from '~/components/ui/icon/IconItem'

export default function OrganisasjonListe({ orgListe }) {
	const columns = [
		{
			text: 'Orgnr.',
			width: '15',
			dataField: 'orgnr',
			unique: true
		},
		{
			text: 'Navn',
			width: '40',
			dataField: 'navn'
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
			dataField: 'status'
		}
	]

	return (
		<ErrorBoundary>
			<DollyTable
				data={orgListe}
				columns={columns}
				pagination
				iconItem={<OrganisasjonItem />}
				onExpand={null}
			/>
		</ErrorBoundary>
	)
}
