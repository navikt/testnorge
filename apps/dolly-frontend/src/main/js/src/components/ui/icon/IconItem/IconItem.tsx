import Icon from '@/components/ui/icon/Icon'
import './IconItem.less'
import { Button } from '@navikt/ds-react'

export const IconItem = ({
	className,
	icon,
	fontSize,
}: {
	className: string
	icon: string
	fontSize?: string
}) => (
	<div className={`icon-item ${className}`}>
		<Button
			data-color="neutral"
			variant={'tertiary'}
			size={'small'}
			style={{ pointerEvents: 'none' }}
		>
			<Icon kind={icon} fontSize={fontSize} />
		</Button>
	</div>
)
