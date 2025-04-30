import * as _ from 'lodash-es'
import styled from 'styled-components'
import { Box } from '@navikt/ds-react'

const LabelValue = styled.div`
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 0.9rem;
	align-content: baseline;

	&& {
		h4 {
			margin: 0;
			font-size: 0.9em;
			text-transform: uppercase;
			width: 150px;
			align-self: end;
		}

		//div {
		//	font-size: 0.9em;
		//}
	}
`

const LabelValueVisning = ({ label, value }) => {
	if (!value) {
		return null
	}
	//TODO: check if value is an array and display it as a list
	return (
		<LabelValue>
			<h4>{label}</h4>
			{Array.isArray(value) ? (
				<div>
					{value.map((item, idx) => (
						<div key={item + idx}>{item}</div>
					))}
				</div>
			) : (
				<div>{value}</div>
			)}
		</LabelValue>
	)
}

const DataVisning = ({ system }) => (
	<>
		<LabelValueVisning label="register" value={system.register} />
		<LabelValueVisning label="namespace" value={system.namespace} />
		<LabelValueVisning label="cluster" value={system.cluster} />
		<LabelValueVisning label="kubernetes-name" value={_.get(system, 'kubernetes-name')} />
		<LabelValueVisning label="url" value={system.url || system.write?.url} />
	</>
)

const ReadWriteDeleteVisning = ({ data, type }) => {
	if (!data) {
		return null
	}

	const backgroundColor =
		type === 'read'
			? 'surface-info-subtle'
			: type === 'write'
				? 'surface-success-subtle'
				: 'surface-danger-subtle'

	return (
		<>
			<Box padding="4" background={backgroundColor}>
				<h3>{type}</h3>
				<DataVisning system={data} />
			</Box>
		</>
	)
}

export const SystemVisning = ({ system }) => {
	return (
		<>
			<DataVisning system={system} />
			<ReadWriteDeleteVisning data={system.read} type="read" />
			<ReadWriteDeleteVisning data={system.write} type="write" />
			<ReadWriteDeleteVisning data={system.delete} type="delete" />
		</>
	)
}
