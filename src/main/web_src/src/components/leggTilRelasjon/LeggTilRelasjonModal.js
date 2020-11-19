import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import LeggTilRelasjonConnector from './LeggTilRelasjonConnector'

import './LeggTilRelasjon.less'

export const LeggTilRelasjonModal = ({ environments, personInfo }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<div className="flexbox--align-center--justify-end">
			<Button onClick={openModal} className="flexbox--align-center" kind="relasjoner">
				LEGG TIL RELASJONER
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="70%" overflow="auto">
				<h1>Legg til relasjoner </h1>
				<div className="relasjon-modal-overskrift">
					<div className="icon">
						<Icon kind={personInfo.kjonn === 'K' ? 'woman' : 'man'} />
					</div>
					<h2>{`${personInfo.fornavn} ${personInfo.etternavn} (${personInfo.ident})`}</h2>
				</div>
				<LeggTilRelasjonConnector
					environments={environments}
					hovedIdent={personInfo.ident}
					closeModal={closeModal}
				/>
			</DollyModal>
		</div>
	)
}
