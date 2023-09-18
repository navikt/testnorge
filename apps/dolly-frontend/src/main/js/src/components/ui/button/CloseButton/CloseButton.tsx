import { Button } from '@navikt/ds-react'
import Icon from '@/components/ui/icon/Icon'

const CloseButton = ({ ...props }) => (
	<Button
		variant={'tertiary'}
		size={'xsmall'}
		style={{ position: 'absolute', top: 2, right: 2 }}
		{...props}
	>
		<Icon kind={'kryss'} fontSize={'1.5rem'} />
	</Button>
)

export default CloseButton
