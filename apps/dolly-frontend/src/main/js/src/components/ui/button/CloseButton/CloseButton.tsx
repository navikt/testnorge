import { Button } from '@navikt/ds-react'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'

const CloseButton = ({ ...props }) => (
	<Button
		data-testid={TestComponentSelectors.BUTTON_MODAL_CLOSE}
		variant={'tertiary'}
		size={'xsmall'}
		style={{ position: 'absolute', top: 2, right: 2 }}
		{...props}
	>
		<Icon kind={'kryss'} fontSize={'1.5rem'} title={props.title} />
	</Button>
)

export default CloseButton
