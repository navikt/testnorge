import React, { useCallback } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import useBoolean from '~/utils/hooks/useBoolean'
import { BankkontoApi, TpsMessagingApi } from '~/service/Api'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import Icon from '~/components/ui/icon/Icon'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'

type Data = {
	data: NorskBankkontoData
	extraButtons?: boolean
	ident?: string
}

type NorskBankkontoData = {
	kontonummer: string
}

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 0;
	top: 0;
	margin: -5px 10px 0 0;
	&&& {
		button {
			position: relative;
		}
	}
`

export const Visning = ({ data, ident, extraButtons }: Data) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [show, , setHide] = useBoolean(true)

	const handleDelete = useCallback(() => {
		const slett = async () => {
			const kontoregister = BankkontoApi.slettKonto(ident)
			const tpsMessaging = TpsMessagingApi.deleteBankkontoNorsk(ident)
			await Promise.all([kontoregister, tpsMessaging]).catch((e) => console.error(e))
			setHide()
		}
		if (ident) {
			return slett()
		} else {
			return
		}
	}, [])

	if (!show) {
		return null
	}

	const mapBankkontoNummer = (nummer: string) => {
		if (!nummer || nummer?.length < 11) {
			return nummer
		} else {
			return nummer.slice(0, 4) + '.' + nummer.slice(4, 6) + '.' + nummer.slice(6)
		}
	}

	return (
		<div style={{ position: 'relative' }}>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue title={'Kontonummer'} value={mapBankkontoNummer(data.kontonummer)} />
				</ErrorBoundary>
			</div>
			{extraButtons && (
				<EditDeleteKnapper>
					<Button kind="trashcan" onClick={() => openModal()} title="Slett" />
					<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
						<div className="slettModal">
							<div className="slettModal slettModal-content">
								<Icon size={50} kind="report-problem-circle" />
								<h1>Slett</h1>
								<h4>Er du sikker på at du vil slette denne bankkontoen fra personen?</h4>
							</div>
							<div className="slettModal-actions">
								<NavButton onClick={closeModal}>Nei</NavButton>
								<NavButton
									onClick={() => {
										closeModal()
										return handleDelete()
									}}
									variant={'primary'}
								>
									Ja, jeg er sikker
								</NavButton>
							</div>
						</div>
					</DollyModal>
				</EditDeleteKnapper>
			)}
		</div>
	)
}

export const NorskBankkonto = ({ data, ident, extraButtons = null }: Data) => {
	if (!data) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Norsk bankkonto" iconKind="bankkonto" />
			<Visning data={data} ident={ident} extraButtons={extraButtons} />
		</div>
	)
}
