import withBreadcrumbs from 'react-router-breadcrumbs-hoc'
import routes from '~/Routes'
import { Breadcrumbs } from './Breadcrumb'

export default withBreadcrumbs(routes, { disableDefaults: true })(Breadcrumbs)
