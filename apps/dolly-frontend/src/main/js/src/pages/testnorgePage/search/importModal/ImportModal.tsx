import React, { useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import { useNavigate } from 'react-router-dom'
import DollyModal from '~/components/ui/modal/DollyModal'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { ImportPerson } from '~/pages/testnorgePage/search/SearchView'
import { DollyApi } from '~/service/Api'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import './ImportModal.less'
import Icon from '~/components/ui/icon/Icon'

type Props = {
	valgtePersoner: ImportPerson[]
	importerPersoner: (valgtePersoner: ImportPerson[], navigate: Function) => void
}

const getPdlPersoner = async (identer: string[]) => {
	return DollyApi.getPersonerFraPdl(identer)
		.then((response: any) => {
			const identerBolk = response?.data?.data?.hentIdenterBolk?.reduce(
				(map: any, person: any) => ({
					...map,
					[person.ident]: person.identer,
				}),
				{}
			)
			return response.data?.data?.hentPersonBolk?.map((ident: any) => {
				return {
					ident: ident.ident,
					data: {
						hentPerson: ident.person,
						hentIdenter: { identer: identerBolk?.[ident.ident] },
					},
				}
			})
		})
		.catch((_e: Error) => {
			return identer.map((ident) => {
				return {
					ident: ident,
					data: null,
				}
			})
		})
}

const partnerSivilstander = ['GIFT', 'SEPARERT']

export const ImportModal = ({ valgtePersoner, importerPersoner }: Props) => {
	const navigate = useNavigate()

	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [partnerIdenter, setPartnerIdenter] = useState([])

	const personerValgt = valgtePersoner.length > 0

	const getPartnere = (data: PdlData[]) => {
		return data
			.map((person) => person?.hentPerson?.sivilstand)
			.map((sivilstand) => {
				let giftSivilstand = sivilstand.filter(
					(siv) => !siv?.metadata?.historisk && partnerSivilstander.includes(siv?.type)
				)
				if (giftSivilstand !== null && giftSivilstand.length > 0) {
					return giftSivilstand[0].relatertVedSivilstand
				} else {
					return null
				}
			})
			.filter((partnerIdent) => partnerIdent !== null)
	}

	const handleImport = () => {
		const partnere = getPartnere(valgtePersoner.map((person) => person.data))
		if (partnere !== null && partnere.length > 0) {
			setPartnerIdenter(partnere)
			openModal()
		} else {
			importer(valgtePersoner)
		}
	}

	const importer = (personer: ImportPerson[]) => {
		importerPersoner(personer, navigate)
	}

	return (
		<React.Fragment>
			<div className="flexbox--align-center--justify-end">
				<NavButton
					type="hoved"
					onClick={handleImport}
					disabled={!personerValgt}
					title={!personerValgt ? 'Velg personer' : null}
				>
					Importer
				</NavButton>
			</div>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="importModal">
					<Icon kind="personinformasjon" size={60} />
					<div className="importModal importModal-content">
						<h1>Import av partner</h1>
						<h4>
							En eller flere av dine valgte Test-Norge personer har en partner. <br /> Vil du
							inkludere partnerne i importen?
						</h4>
					</div>
					<div className="importModal-actions">
						<NavButton
							onClick={() => {
								getPdlPersoner(partnerIdenter).then((response: ImportPerson[]) => {
									closeModal()
									importerPersoner(valgtePersoner.concat(response), navigate)
								})
							}}
							type="hoved"
						>
							Ja
						</NavButton>
						<NavButton onClick={() => importer(valgtePersoner)}>Nei</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
