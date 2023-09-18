import styled from 'styled-components'
import Icon from '@/components/ui/icon/Icon'
import './DollyKjede.less'

export interface KjedeIconProps {
	locked: boolean
	onClick: () => void
}

const KjedeIcon = styled.div`
	border-radius: 2px;
	display: block;
	margin: 2px 2px 2px 6px;
	padding: 5px 5px 3px 5px;
	cursor: pointer;
	fill: white;
`

export default ({ locked, onClick }: KjedeIconProps) => {
	return (
		<KjedeIcon onClick={() => onClick()} className={'background-color-lightblue'}>
			<Icon
				style={{ stroke: 'white', strokeWidth: '1.5px' }}
				fontSize={'1.6rem'}
				kind={locked ? 'link' : 'link-broken'}
			/>
		</KjedeIcon>
	)
}
