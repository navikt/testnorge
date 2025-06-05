import styled from 'styled-components'

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

export const LabelValueColumns = ({ label, value }: { label: string; value: string }) => {
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
