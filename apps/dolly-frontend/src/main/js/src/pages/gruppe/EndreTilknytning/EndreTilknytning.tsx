import React, { useState } from 'react'
import { EndreTilknytningDialog } from '@/pages/gruppe/EndreTilknytning/EndreTilknytningDialog'
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
			{open && <EndreTilknytningDialog gruppe={gruppe} open={open} setOpen={setOpen} />}
		</>
	)
}
