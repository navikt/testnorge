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
	action: Function
	partnerIdent: string
	gruppeId: string
	gruppeIdenter: string[]
}

export const PartnerImportButton = ({ action, gruppeId, partnerIdent, gruppeIdenter }: Props) => {
	if (!partnerIdent) return null
	const [loading, setLoading] = useState(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	const disabled = gruppeIdenter.includes(partnerIdent)

	const handleImport = async (gruppeId: string, ident: string) => {
		setLoading(true)
		await DollyApi.importerPartner(gruppeId, ident)
			.then((response) => {
				setLoading(false)
				action()
			})
			.catch((error) => {
				setLoading(false)
			})
	}

	if (loading) return <Loading label="importerer..." />

	return (
		<React.Fragment>
			<Button
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Partner er allerede i gruppen' : ''}
				kind="relasjoner"
			>
				IMPORTER PARTNER
			</Button>
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
								handleImport(gruppeId, partnerIdent)
							}}
							type="hoved"
						>
							Ja
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
