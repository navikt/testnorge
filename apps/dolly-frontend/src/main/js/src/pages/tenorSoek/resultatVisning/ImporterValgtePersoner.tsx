import { useNavigate } from 'react-router-dom'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { Button, Checkbox, Modal } from '@navikt/ds-react'
import React, { useState } from 'react'
import { MalValg } from '@/pages/tenorSoek/resultatVisning/MalValg'
import { EnterIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'

export const ImporterValgtePersoner = ({ identer, isMultiple }) => {
	const navigate = useNavigate()
	const { pdlPersoner, loading, error } = usePdlPersonbolk(identer)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
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
			{isMultiple ? (
				<Button
					variant="primary"
					size="small"
					disabled={identer?.length < 1}
					loading={loading}
					onClick={openModal}
				>
					{identer?.length === 1
						? 'Importer 1 valgt person'
						: `Importer ${identer?.length} valgte personer`}
				</Button>
			) : (
				<Button
					variant="tertiary"
					size="xsmall"
					icon={<EnterIcon />}
					loading={loading}
					onClick={openModal}
					style={{ minWidth: '150px' }}
				>
					Importer person
				</Button>
			)}
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<div>
					<h1>{identer?.length === 1 ? 'Importer person' : 'Importer personer'}</h1>
					<div style={{ margin: '20px 0' }}>
						<Checkbox size="small">Inkluder partnere</Checkbox>
					</div>
					<MalValg setValgtMal={setValgtMal} />
					<div className="dollymodal_buttons dollymodal_buttons--center">
						<Button onClick={() => handleClick()}>Importer</Button>
						<Button variant="secondary" onClick={closeModal}>
							Avbryt
						</Button>
					</div>
				</div>
			</DollyModal>
		</>
	)
}
