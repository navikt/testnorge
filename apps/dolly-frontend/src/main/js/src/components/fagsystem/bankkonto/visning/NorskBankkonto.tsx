import React, { useCallback } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import useBoolean from '@/utils/hooks/useBoolean'
import { BankkontoApi, TpsMessagingApi } from '@/service/Api'
import styled from 'styled-components'
import { formatDate } from '@/utils/DataFormatter'
import { BekreftSlettDialog } from '@/components/ui/button/SlettDialog/BekreftSlettDialog'

type Data = {
	data: NorskBankkontoData
	extraButtons?: boolean
	ident?: string
}

type NorskBankkontoData = {
	kontonummer: string
	gyldig: string
}

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 8px;
	top: 0;
`

export const Visning = ({ data, ident, extraButtons }: Data) => {
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
		if (!nummer || nummer?.length !== 11) {
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
					<TitleValue title={'Gyldig f.o.m.'} value={formatDate(data.gyldig)} />
				</ErrorBoundary>
			</div>
			{extraButtons && (
				<EditDeleteKnapper>
					<BekreftSlettDialog
						tittel="Er du sikker på at du vil slette bankkontoen?"
						beskrivelse="Du er i ferd med å slette en bankkonto fra personen. Denne handlingen kan ikke angres."
						onBekreft={handleDelete}
					/>
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
