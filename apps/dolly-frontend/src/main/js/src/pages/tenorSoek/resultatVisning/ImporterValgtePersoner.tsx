import { useNavigate } from 'react-router-dom'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { Button, Checkbox, Modal } from '@navikt/ds-react'
import React, { useRef, useState } from 'react'
import { MalValg } from '@/pages/tenorSoek/resultatVisning/MalValg'

export const ImporterValgtePersoner = ({ identer }) => {
	const navigate = useNavigate()
	const { pdlPersoner, loading, error } = usePdlPersonbolk(identer)

	const ref = useRef<HTMLDialogElement>(null)
	const [valgtMal, setValgtMal] = useState(null)

	const handleClick = () => {
		navigate(`/importer`, {
			state: {
				importPersoner: identer.map((ident) => {
					return {
						ident: ident,
						data: {
							hentPerson: pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)?.person,
							hentIdenter: pdlPersoner?.hentIdenterBolk?.find((p) => p.ident === ident)?.identer,
						},
					}
				}),
				mal: valgtMal,
				// gruppe: gruppe,
			},
		})
	}

	return (
		<>
			<Button
				variant="primary"
				size="small"
				disabled={identer?.length < 1}
				loading={loading}
				onClick={() => ref.current?.showModal()}
			>
				{identer?.length === 1
					? 'Importer 1 valgt person'
					: `Importer ${identer?.length} valgte personer`}
			</Button>
			<Modal ref={ref} header={{ heading: 'Importer personer' }} width={750} closeOnBackdropClick>
				<Modal.Body>
					<Checkbox>Inkluder partnere</Checkbox>
					<MalValg setValgtMal={setValgtMal} />
				</Modal.Body>
				<Modal.Footer>
					<Button onClick={() => handleClick()}>Importer</Button>
					<Button variant="secondary" onClick={() => ref.current?.close()}>
						Avbryt
					</Button>
				</Modal.Footer>
			</Modal>
		</>
	)
}
