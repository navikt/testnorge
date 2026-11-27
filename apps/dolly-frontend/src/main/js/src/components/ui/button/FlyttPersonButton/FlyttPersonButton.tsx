import React, { useState } from 'react'
import { FlyttPersonDialog } from '@/components/ui/button/FlyttPersonButton/FlyttPersonDialog'
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
			{open && <FlyttPersonDialog gruppeId={gruppeId} open={open} setOpen={setOpen} />}
		</>
	)
}
