import React from 'react'
import { Button } from '@navikt/ds-react'
import Icon from '~/components/ui/icon/Icon'

const CloseButton = ({ ...props }) => (
	<Button
		variant={'tertiary'}
		size={'xsmall'}
		style={{ position: 'absolute', top: 2, right: 2 }}
		{...props}
	>
		<Icon kind={'kryss'} />
	</Button>
)

export default CloseButton
