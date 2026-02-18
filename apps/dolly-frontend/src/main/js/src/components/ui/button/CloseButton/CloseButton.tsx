import { Button } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { XMarkIcon } from '@navikt/aksel-icons'

const CloseButton = ({ ...props }) => (
	<Button
		data-testid={TestComponentSelectors.BUTTON_MODAL_CLOSE}
		variant={'tertiary'}
		size={'small'}
		icon={<XMarkIcon title="Lukk" />}
		style={{ position: 'absolute', top: 2, right: 2 }}
		{...props}
	/>
)

export default CloseButton
