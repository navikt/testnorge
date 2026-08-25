import React, { useState } from 'react'
import { EndreTilknytningModal } from '@/pages/gruppe/EndreTilknytning/EndreTilknytningModal'
import { Button } from '@navikt/ds-react'
import { PersonPencilIcon } from '@navikt/aksel-icons'
import { Gruppe } from '@/utils/hooks/useGruppe'

type EndreTilknytningProps = {
	gruppe: Gruppe
}

export const EndreTilknytning = ({ gruppe }: EndreTilknytningProps) => {
	const [open, setOpen] = useState(false)

	return (
		<>
			<Button
				size="xsmall"
				variant="tertiary"
				icon={<PersonPencilIcon aria-hidden />}
				onClick={() => setOpen(true)}
			>
				Bytt eier
			</Button>
			<EndreTilknytningModal gruppe={gruppe} open={open} setOpen={setOpen} />
		</>
	)
}
