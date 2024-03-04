import { useNavigate } from 'react-router-dom'
import { useNaviger } from '@/utils/hooks/useNaviger'
import { Button } from '@navikt/ds-react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { ArrowRightIcon } from '@navikt/aksel-icons'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'

export const ImporterPerson = ({ valgtPerson }) => {
	const navigate = useNavigate()
	const { result, loading } = useNaviger(valgtPerson)
	const { pdlPersoner, loading: loadingPdl, error: errorPdl } = usePdlPersonbolk(valgtPerson)

	// TODO vis loading?
	if (result) {
		return null
	}

	const handleClick = (event) => {
		// event.stopPropagation()
		navigate(`/importer`, {
			state: {
				importPersoner: [
					{
						ident: valgtPerson,
						data: {
							hentPerson: pdlPersoner?.hentPersonBolk?.find((p) => p.ident === valgtPerson)?.person,
							hentIdenter: pdlPersoner?.hentIdenterBolk?.find((p) => p.ident === valgtPerson)
								?.identer,
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
			data-cy={CypressSelector.BUTTON_VIS_I_GRUPPE}
			variant="tertiary"
			size="xsmall"
			icon={<ArrowRightIcon />}
			loading={loading || loadingPdl}
			onClick={handleClick}
			style={{ minWidth: '150px' }}
		>
			Importer til gruppe
		</Button>
	)
}
