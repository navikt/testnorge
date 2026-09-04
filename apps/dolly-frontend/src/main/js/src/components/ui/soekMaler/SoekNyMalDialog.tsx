import React, { useState } from 'react'
import { Button } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { FileLoadingIcon } from '@navikt/aksel-icons'
import { MalModal, malTyper } from '@/pages/minSide/maler/MalModal'

export const SoekNyMalDialog = ({ id, malType }) => {
	const [open, setOpen] = useState(false)

	// TODO: Disabled dersom ingen soekeverdier er satt
	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL}
				size="small"
				variant="secondary"
				// icon={<FileLoadingIcon aria-hidden />}
				onClick={() => setOpen(true)}
			>
				Opprett mal fra søk
			</Button>
			<MalModal id={id} malType={malType} open={open} setOpen={setOpen} />
		</>
	)
}
