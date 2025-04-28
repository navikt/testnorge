import * as _ from 'lodash-es'
import styled from 'styled-components'

const LabelValue = styled.div`
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 0.8rem;
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

	return (
		<LabelValue>
			<h4>{label}</h4>
			<div>{value}</div>
		</LabelValue>
	)
}

export const SystemVisning = ({ system }) => {
	return (
		<>
			<LabelValueVisning label="register" value={system.register} />
			<LabelValueVisning label="namespace" value={system.namespace} />
			<LabelValueVisning label="cluster" value={system.cluster} />
			<LabelValueVisning label="kubernetes-name" value={_.get(system, 'kubernetes-name')} />
			<LabelValueVisning label="url" value={system.url} />
		</>
	)
}
