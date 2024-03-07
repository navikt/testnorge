import { useNavigate } from 'react-router-dom'
import { useNaviger } from '@/utils/hooks/useNaviger'
import { Button } from '@navikt/ds-react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { ArrowRightIcon, EnterIcon } from '@navikt/aksel-icons'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'

export const ImporterPerson = ({ ident }) => {
	const navigate = useNavigate()
	// const { result, loading } = useNaviger(ident)
	const { pdlPersoner, loading, error } = usePdlPersonbolk(ident)

	// TODO vis loading?
	// if (result) {
	// 	return null
	// }
	// console.log('ident: ', ident) //TODO - SLETT MEG
	// console.log('pdlPersoner: ', pdlPersoner) //TODO - SLETT MEG
	const handleClick = (event) => {
		// event.stopPropagation()
		navigate(`/importer`, {
			state: {
				importPersoner: [
					{
						ident: ident,
						data: {
							hentPerson: pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)?.person,
							hentIdenter: pdlPersoner?.hentIdenterBolk?.find((p) => p.ident === ident)?.identer,
						},
					},
				],
				// mal: mal,
				// gruppe: gruppe,
			},
		})
	}

	return (
		<Button
			// data-cy={CypressSelector.BUTTON_VIS_I_GRUPPE}
			variant="tertiary"
			size="xsmall"
			icon={<EnterIcon />}
			loading={loading}
			onClick={handleClick}
			style={{ minWidth: '150px' }}
		>
			Importer til gruppe
		</Button>
	)
}
