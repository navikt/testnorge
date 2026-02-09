import './IconItem.less'
import { Button } from '@navikt/ds-react'

export const IconItem = ({ className, icon }: { className: string; icon: React.ReactNode }) => (
	<div className={`icon-item ${className}`}>
		<Button
			data-color="neutral"
			variant={'tertiary'}
			size={'small'}
			style={{ pointerEvents: 'none' }}
			icon={icon}
		/>
	</div>
)
