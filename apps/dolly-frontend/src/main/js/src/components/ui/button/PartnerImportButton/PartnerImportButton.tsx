import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import { DollyApi } from '~/service/Api'
import './PartnerImportButton.less'

type Props = {
	partnerIdent: string
	gruppeId: string
	gruppeIdenter: string[]
	master: string
}

export const PartnerImportButton = ({ gruppeId, partnerIdent, gruppeIdenter, master }: Props) => {
	const [loading, setLoading] = useState(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [feilmelding, setFeilmelding] = useState(null)
	const [fullfoert, setFullfoert] = useState(false)

	if (!partnerIdent) {
		return null
	}

	const disabled = gruppeIdenter.includes(partnerIdent)

	const handleImport = async (ident: string) => {
		setLoading(true)
		setFeilmelding(null)
		await DollyApi.importerPartner(gruppeId, ident, master)
			.then((_response) => {
				setLoading(false)
				setFullfoert(true)
			})
			.catch((_error) => {
				setFeilmelding('Noe gikk galt')
				setFullfoert(false)
				setLoading(false)
			})
	}

	if (loading) {
		return <Loading label="importerer..." />
	}

	if (fullfoert) {
		return (
			<div className={'success-text'}>
				<Icon size={16} kind={'feedback-check-circle'} />
				<span>VENNLIGST LUKK VISNING</span>
			</div>
		)
	}

	return (
		<div>
			<Button
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Partner er allerede i gruppen' : ''}
				kind="relasjoner"
				className="svg-icon-blue"
			>
				IMPORTER PARTNER
			</Button>
			{feilmelding && (
				<div className="error-message" style={{ margin: '5px 0 0 30px' }}>
					{feilmelding}
				</div>
			)}
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="partnerImportModal">
					<div className="partnerImportModal partnerImportModal-content">
						<Icon size={50} kind="personinformasjon" />
						<h1>Importer partner</h1>
						<h4>
							Er du sikker på at du vil importere og legge til valgt persons partner i gruppen?
						</h4>
					</div>
					<div className="partnerImportModal-actions">
						<NavButton onClick={closeModal}>Nei</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								handleImport(partnerIdent)
							}}
							type="hoved"
						>
							Ja
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</div>
	)
}
