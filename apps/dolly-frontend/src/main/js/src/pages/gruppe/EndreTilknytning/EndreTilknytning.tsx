import Button from '@/components/ui/button/Button'
import React, { useState } from 'react'
import { EndreTilknytningModal } from '@/pages/gruppe/EndreTilknytning/EndreTilknytningModal'

export const EndreTilknytning = ({ gruppe }) => {
	const [open, setOpen] = useState(false)

	return (
		<>
			{/*TODO: Endre Button til Aksel-button*/}
			<Button onClick={() => setOpen(true)} kind="rediger-person">
				BYTT EIER
			</Button>
			<EndreTilknytningModal gruppe={gruppe} open={open} setOpen={setOpen} />
		</>
	)
}
