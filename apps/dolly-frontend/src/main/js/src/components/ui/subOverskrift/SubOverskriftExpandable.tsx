import Icon from '@/components/ui/icon/Icon'
import useBoolean from '@/utils/hooks/useBoolean'
import styled from 'styled-components'

type Props = {
	iconKind?: string
	label?: string
	isExpanded?: boolean
	children?: any
}

const Overskrift = styled.div`
	background-color: var(--ax-bg-info-moderate);
	margin: 5px 0 15px;
	display: flex;
	align-items: center;
	padding: 4px;
	cursor: pointer;

	h3 {
		font-size: 1em;
		margin: 0;
		width: 100%;
	}

	svg {
		margin-right: 5px;
	}
`

export default function SubOverskriftExpandable({
	iconKind,
	label,
	isExpanded = false,
	children,
}: Props) {
	if (!label) {
		return null
	}

	const [expanded, setExpanded, setCollapsed] = useBoolean(isExpanded)

	return (
		<>
			<Overskrift onClick={expanded ? setCollapsed : setExpanded}>
				{iconKind && <Icon fontSize={'1.5rem'} kind={iconKind} />}
				<h3>{label}</h3>
				<Icon fontSize={'1.5rem'} kind={expanded ? 'chevron-up' : 'chevron-down'} />
			</Overskrift>
			{expanded && children}
		</>
	)
}
