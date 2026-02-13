import * as _ from 'lodash-es'
import styled from 'styled-components'
import { Box } from '@navikt/ds-react'
import { LabelValueColumns } from '@/components/ui/labelValueColumns/LabelValueColumns'

const ReadWriteDelete = styled.div`
	margin-top: 1rem;
	&& {
		h3 {
			margin-top: 0;
			font-size: 1.125rem;
			text-transform: capitalize;
		}
		h4 {
			width: 148px;
		}
	}
`

const DataVisning = ({ system }: { system: any }) => (
	<>
		<LabelValueColumns label="register" value={system.register} />
		<LabelValueColumns label="namespace" value={system.namespace} />
		<LabelValueColumns label="name" value={system.name} />
		<LabelValueColumns label="cluster" value={system.cluster} />
		<LabelValueColumns label="kubernetes-name" value={_.get(system, 'kubernetes-name')} />
		<LabelValueColumns label="url" value={system.url} />
		<LabelValueColumns label="documentation" value={system.documentation} />
		<LabelValueColumns label="description" value={system.description} />
	</>
)

const ReadWriteDeleteVisning = ({ data, type }: { data: any; type: string }) => {
	if (!data) {
		return null
	}

	const nestedData = _.get(data, type)

	const backgroundColor =
		type === 'read' ? 'info-moderate' : type === 'write' ? 'success-moderate' : 'danger-moderate'

	return (
		<ReadWriteDelete>
			<Box padding="space-12" background={backgroundColor}>
				<h3>{type}</h3>
				<DataVisning system={nestedData ? { ...data, ...nestedData } : data} />
			</Box>
		</ReadWriteDelete>
	)
}

export const SystemVisning = ({ system }: { system: any }) => {
	return (
		<>
			<DataVisning system={system} />
			<ReadWriteDeleteVisning data={system.read} type="read" />
			<ReadWriteDeleteVisning data={system.write} type="write" />
			<ReadWriteDeleteVisning data={system.delete} type="delete" />
		</>
	)
}
