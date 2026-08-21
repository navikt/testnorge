import { MalModal } from '@/pages/minSide/maler/MalModal'
import React, { useState } from 'react'
import { FileLoadingIcon } from '@navikt/aksel-icons'
import { Button } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const OpprettMal = ({ id, malType }: { id: string; malType: string }) => {
	const [open, setOpen] = useState(false)

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL} //TODO: Tilpasse denne for alle stedene OpprettMal blir brukt?
				size="xsmall"
				variant="tertiary"
				icon={<FileLoadingIcon aria-hidden />}
				onClick={() => setOpen(true)}
			>
				Opprett mal
			</Button>
			<MalModal id={id} malType={malType} open={open} setOpen={setOpen} />
		</>
	)
}
