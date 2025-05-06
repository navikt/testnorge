import * as _ from 'lodash-es'
import styled from 'styled-components'
import { Box } from '@navikt/ds-react'

const LabelValue = styled.div`
	display: flex;
	flex-wrap: nowrap;
	margin-bottom: 0.8rem;

	&& {
		h4 {
			margin: 0;
			font-size: 0.9em;
			transform: translateY(15%);
			text-transform: uppercase;
			color: #525962;
			width: 160px;
			align-self: baseline;
		}
		.verdi {
			width: 80%;
		}
	}
`

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

const LabelValueVisning = ({ label, value }: { label: string; value: string }) => {
	if (!value) {
		return null
	}

	const formatValue = (value: string) => {
		if (value.startsWith('http')) {
			return (
				<a href={value} target="_blank">
					{value}
				</a>
			)
		}
		return value
	}

	return (
		<LabelValue>
			<h4>{label}</h4>
			{Array.isArray(value) ? (
				<div className="verdi">
					{value.map((item, idx) => (
						<div key={item + idx}>{formatValue(item)}</div>
					))}
				</div>
			) : (
				<div className="verdi">{formatValue(value)}</div>
			)}
		</LabelValue>
	)
}

const DataVisning = ({ system }: { system: any }) => (
	<>
		<LabelValueVisning label="register" value={system.register} />
		<LabelValueVisning label="namespace" value={system.namespace} />
		<LabelValueVisning label="name" value={system.name} />
		<LabelValueVisning label="cluster" value={system.cluster} />
		<LabelValueVisning label="kubernetes-name" value={_.get(system, 'kubernetes-name')} />
		<LabelValueVisning label="url" value={system.url} />
		<LabelValueVisning label="documentation" value={system.documentation} />
		<LabelValueVisning label="description" value={system.description} />
	</>
)

const ReadWriteDeleteVisning = ({ data, type }: { data: any; type: string }) => {
	if (!data) {
		return null
	}

	const nestedData = _.get(data, type)

	const backgroundColor =
		type === 'read'
			? 'surface-info-subtle'
			: type === 'write'
				? 'surface-success-subtle'
				: 'surface-danger-subtle'

	return (
		<ReadWriteDelete>
			<Box padding="3" background={backgroundColor}>
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
