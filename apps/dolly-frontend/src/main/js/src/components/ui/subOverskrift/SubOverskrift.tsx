import Icon from '@/components/ui/icon/Icon'
import './SubOverskrift.less'

type Props = {
	iconKind?: string
	label?: string
	isWarning?: boolean
	style?: any
}

export default function SubOverskrift({ iconKind, label, isWarning = false, style }: Props) {
	if (!label) {
		return null
	}
	return (
		<div className={`sub-overskrift${isWarning ? ' warning' : ''}`} style={style}>
			{iconKind && <Icon fontSize={'1.5rem'} kind={iconKind} />}
			<h3>{label}</h3>
		</div>
	)
}
