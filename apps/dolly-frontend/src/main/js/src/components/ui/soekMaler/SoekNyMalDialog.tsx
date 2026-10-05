import React, { useState } from 'react'
import { Button } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { MalModal } from '@/pages/minSide/maler/MalModal'

interface SoekNyMalDialogProps {
	verdier: Record<string, unknown>
	malType: string
	disabled: boolean
}

export const SoekNyMalDialog = ({ verdier, malType, disabled }: SoekNyMalDialogProps) => {
	const [open, setOpen] = useState(false)

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL}
				size="small"
				variant="secondary"
				disabled={disabled}
				onClick={() => setOpen(true)}
			>
				Opprett mal fra søk
			</Button>
			<MalModal verdier={verdier} malType={malType} open={open} setOpen={setOpen} />
		</>
	)
}
