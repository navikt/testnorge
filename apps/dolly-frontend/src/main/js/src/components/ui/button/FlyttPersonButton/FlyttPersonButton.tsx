import React, { useState } from 'react'
import { FlyttPersonModal } from '@/components/ui/button/FlyttPersonButton/FlyttPersonModal'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Button } from '@navikt/ds-react'
import { LeaveIcon } from '@navikt/aksel-icons'

export const FlyttPersonButton = ({
	gruppeId,
	disabled,
}: {
	gruppeId: string
	disabled: boolean
}) => {
	const [open, setOpen] = useState(false)

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER}
				size="xsmall"
				variant="tertiary"
				icon={<LeaveIcon aria-hidden />}
				onClick={() => setOpen(true)}
				disabled={disabled}
				title={disabled ? 'Kan ikke flytte personer fra en tom gruppe' : undefined}
			>
				Flytt personer
			</Button>
			<FlyttPersonModal gruppeId={gruppeId} open={open} setOpen={setOpen} />
		</>
	)
}
